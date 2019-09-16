package com.opengarden.firechat.matrixsdk.crypto;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.IMXDecrypting;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.IMXEncrypting;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXEncryptEventContentResult;
import com.opengarden.firechat.matrixsdk.crypto.data.MXKey;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmSessionResult;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.cryptostore.IMXCryptoStore;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.EventContent;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeysUploadResponse;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyRequest;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyRequestBody;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.matrix.olm.OlmAccount;

public class MXCrypto {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXCrypto";
    private static final int ONE_TIME_KEY_GENERATION_MAX_NUMBER = 5;
    private static final long ONE_TIME_KEY_UPLOAD_PERIOD = 60000;
    /* access modifiers changed from: private */
    public MXCryptoConfig mCryptoConfig;
    public IMXCryptoStore mCryptoStore;
    private Handler mDecryptingHandler = null;
    /* access modifiers changed from: private */
    public HandlerThread mDecryptingHandlerThread = null;
    /* access modifiers changed from: private */
    public final MXDeviceList mDevicesList;
    private Handler mEncryptingHandler = null;
    /* access modifiers changed from: private */
    public HandlerThread mEncryptingHandlerThread = null;
    private final MXEventListener mEventListener = new MXEventListener() {
        public void onToDeviceEvent(Event event) {
            MXCrypto.this.onToDeviceEvent(event);
        }

        public void onLiveEvent(Event event, RoomState roomState) {
            if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_MESSAGE_ENCRYPTION)) {
                MXCrypto.this.onCryptoEvent(event);
            } else if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_STATE_ROOM_MEMBER)) {
                MXCrypto.this.onRoomMembership(event);
            }
        }
    };
    /* access modifiers changed from: private */
    public final ArrayList<ApiCallback<Void>> mInitializationCallbacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean mIsStarted;
    /* access modifiers changed from: private */
    public boolean mIsStarting;
    private long mLastOneTimeKeyCheck = 0;
    /* access modifiers changed from: private */
    public Map<String, Map<String, String>> mLastPublishedOneTimeKeys;
    /* access modifiers changed from: private */
    public MXDeviceInfo mMyDevice;
    /* access modifiers changed from: private */
    public NetworkConnectivityReceiver mNetworkConnectivityReceiver;
    /* access modifiers changed from: private */
    public final IMXNetworkEventListener mNetworkListener = new IMXNetworkEventListener() {
        public void onNetworkConnectionUpdate(boolean z) {
            if (z && !MXCrypto.this.isStarted()) {
                Log.m209d(MXCrypto.LOG_TAG, "Start MXCrypto because a network connection has been retrieved ");
                MXCrypto.this.start(false, null);
            }
        }
    };
    /* access modifiers changed from: private */
    public MXOlmDevice mOlmDevice;
    /* access modifiers changed from: private */
    public boolean mOneTimeKeyCheckInProgress = false;
    /* access modifiers changed from: private */
    public Integer mOneTimeKeyCount;
    /* access modifiers changed from: private */
    public final MXOutgoingRoomKeyRequestManager mOutgoingRoomKeyRequestManager;
    private final List<IncomingRoomKeyRequest> mReceivedRoomKeyRequestCancellations = new ArrayList();
    private final List<IncomingRoomKeyRequest> mReceivedRoomKeyRequests = new ArrayList();
    /* access modifiers changed from: private */
    public final HashMap<String, HashMap<String, IMXDecrypting>> mRoomDecryptors;
    /* access modifiers changed from: private */
    public final HashMap<String, IMXEncrypting> mRoomEncryptors;
    public final Set<IRoomKeysRequestListener> mRoomKeysRequestListeners = new HashSet();
    /* access modifiers changed from: private */
    public final MXSession mSession;
    private Handler mUIHandler = null;
    private boolean mWarnOnUnknownDevices = true;

    public interface IRoomKeysRequestListener {
        void onRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest);

        void onRoomKeyRequestCancellation(IncomingRoomKeyRequestCancellation incomingRoomKeyRequestCancellation);
    }

    public MXCrypto(MXSession mXSession, IMXCryptoStore iMXCryptoStore, @Nullable MXCryptoConfig mXCryptoConfig) {
        HashMap hashMap;
        this.mSession = mXSession;
        this.mCryptoStore = iMXCryptoStore;
        if (mXCryptoConfig != null) {
            this.mCryptoConfig = mXCryptoConfig;
        } else {
            this.mCryptoConfig = new MXCryptoConfig();
        }
        this.mOlmDevice = new MXOlmDevice(this.mCryptoStore);
        this.mRoomEncryptors = new HashMap<>();
        this.mRoomDecryptors = new HashMap<>();
        String str = this.mSession.getCredentials().deviceId;
        boolean z = !TextUtils.isEmpty(str);
        if (TextUtils.isEmpty(str)) {
            Credentials credentials = this.mSession.getCredentials();
            String deviceId = this.mCryptoStore.getDeviceId();
            credentials.deviceId = deviceId;
            str = deviceId;
        }
        if (TextUtils.isEmpty(str)) {
            Credentials credentials2 = this.mSession.getCredentials();
            String uuid = UUID.randomUUID().toString();
            credentials2.deviceId = uuid;
            Log.m209d(LOG_TAG, "Warning: No device id in MXCredentials. An id was created. Think of storing it");
            this.mCryptoStore.storeDeviceId(uuid);
            str = uuid;
        }
        this.mMyDevice = new MXDeviceInfo(str);
        this.mMyDevice.userId = this.mSession.getMyUserId();
        this.mDevicesList = new MXDeviceList(mXSession, this);
        HashMap hashMap2 = new HashMap();
        if (!TextUtils.isEmpty(this.mOlmDevice.getDeviceEd25519Key())) {
            StringBuilder sb = new StringBuilder();
            sb.append("ed25519:");
            sb.append(this.mSession.getCredentials().deviceId);
            hashMap2.put(sb.toString(), this.mOlmDevice.getDeviceEd25519Key());
        }
        if (!TextUtils.isEmpty(this.mOlmDevice.getDeviceCurve25519Key())) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("curve25519:");
            sb2.append(this.mSession.getCredentials().deviceId);
            hashMap2.put(sb2.toString(), this.mOlmDevice.getDeviceCurve25519Key());
        }
        this.mMyDevice.keys = hashMap2;
        this.mMyDevice.algorithms = MXCryptoAlgorithms.sharedAlgorithms().supportedAlgorithms();
        this.mMyDevice.mVerified = 1;
        Map userDevices = this.mCryptoStore.getUserDevices(this.mSession.getMyUserId());
        if (userDevices != null) {
            hashMap = new HashMap(userDevices);
        } else {
            hashMap = new HashMap();
        }
        hashMap.put(this.mMyDevice.deviceId, this.mMyDevice);
        this.mCryptoStore.storeUserDevices(this.mSession.getMyUserId(), hashMap);
        this.mSession.getDataHandler().setCryptoEventsListener(this.mEventListener);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("MXCrypto_encrypting_");
        sb3.append(this.mSession.getMyUserId());
        this.mEncryptingHandlerThread = new HandlerThread(sb3.toString(), 1);
        this.mEncryptingHandlerThread.start();
        StringBuilder sb4 = new StringBuilder();
        sb4.append("MXCrypto_decrypting_");
        sb4.append(this.mSession.getMyUserId());
        this.mDecryptingHandlerThread = new HandlerThread(sb4.toString(), 1);
        this.mDecryptingHandlerThread.start();
        this.mUIHandler = new Handler(Looper.getMainLooper());
        if (z) {
            this.mDevicesList.handleDeviceListsChanges(Arrays.asList(new String[]{this.mSession.getMyUserId()}), null);
        }
        this.mOutgoingRoomKeyRequestManager = new MXOutgoingRoomKeyRequestManager(this.mSession, this);
        this.mReceivedRoomKeyRequests.addAll(this.mCryptoStore.getPendingIncomingRoomKeyRequests());
    }

    public Handler getEncryptingThreadHandler() {
        if (this.mEncryptingHandler == null) {
            this.mEncryptingHandler = new Handler(this.mEncryptingHandlerThread.getLooper());
        }
        if (this.mEncryptingHandler == null) {
            return this.mUIHandler;
        }
        return this.mEncryptingHandler;
    }

    private Handler getDecryptingThreadHandler() {
        if (this.mDecryptingHandler == null) {
            this.mDecryptingHandler = new Handler(this.mDecryptingHandlerThread.getLooper());
        }
        if (this.mDecryptingHandler == null) {
            return this.mUIHandler;
        }
        return this.mDecryptingHandler;
    }

    public Handler getUIHandler() {
        return this.mUIHandler;
    }

    public void setNetworkConnectivityReceiver(NetworkConnectivityReceiver networkConnectivityReceiver) {
        this.mNetworkConnectivityReceiver = networkConnectivityReceiver;
    }

    public boolean isCorrupted() {
        return this.mCryptoStore != null && this.mCryptoStore.isCorrupted();
    }

    public boolean hasBeenReleased() {
        return this.mOlmDevice == null;
    }

    public MXDeviceInfo getMyDevice() {
        return this.mMyDevice;
    }

    public IMXCryptoStore getCryptoStore() {
        return this.mCryptoStore;
    }

    public MXDeviceList getDeviceList() {
        return this.mDevicesList;
    }

    public int getDeviceTrackingStatus(String str) {
        return this.mCryptoStore.getDeviceTrackingStatus(str, -1);
    }

    public boolean isStarted() {
        return this.mIsStarted;
    }

    public boolean isStarting() {
        return this.mIsStarting;
    }

    /* access modifiers changed from: private */
    public void onError(final boolean z) {
        getUIHandler().postDelayed(new Runnable() {
            public void run() {
                if (!MXCrypto.this.isStarted()) {
                    MXCrypto.this.mIsStarting = false;
                    MXCrypto.this.start(z, null);
                }
            }
        }, 1000);
    }

    public void start(final boolean z, ApiCallback<Void> apiCallback) {
        synchronized (this.mInitializationCallbacks) {
            if (apiCallback != null) {
                try {
                    if (this.mInitializationCallbacks.indexOf(apiCallback) < 0) {
                        this.mInitializationCallbacks.add(apiCallback);
                    }
                } finally {
                    while (true) {
                    }
                }
            }
        }
        if (!this.mIsStarting) {
            final C24824 r4 = new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                        public void run() {
                            if (MXCrypto.this.mNetworkConnectivityReceiver != null) {
                                MXCrypto.this.mNetworkConnectivityReceiver.removeEventListener(MXCrypto.this.mNetworkListener);
                            }
                            MXCrypto.this.mIsStarting = false;
                            MXCrypto.this.mIsStarted = true;
                            MXCrypto.this.mOutgoingRoomKeyRequestManager.start();
                            synchronized (MXCrypto.this.mInitializationCallbacks) {
                                Iterator it = MXCrypto.this.mInitializationCallbacks.iterator();
                                while (it.hasNext()) {
                                    final ApiCallback apiCallback = (ApiCallback) it.next();
                                    MXCrypto.this.getUIHandler().post(new Runnable() {
                                        public void run() {
                                            apiCallback.onSuccess(null);
                                        }
                                    });
                                }
                                MXCrypto.this.mInitializationCallbacks.clear();
                            }
                            if (z) {
                                MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                                    public void run() {
                                        MXCrypto.this.getDeviceList().invalidateAllDeviceLists();
                                        MXCrypto.this.mDevicesList.refreshOutdatedDeviceLists();
                                    }
                                });
                            } else {
                                MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                                    public void run() {
                                        MXCrypto.this.processReceivedRoomKeyRequests();
                                    }
                                });
                            }
                        }
                    });
                }

                public void onNetworkError(Exception exc) {
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## start failed : ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                    MXCrypto.this.onError(z);
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## start failed : ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$000, sb.toString());
                    MXCrypto.this.onError(z);
                }

                public void onUnexpectedError(Exception exc) {
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## start failed : ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                    MXCrypto.this.onError(z);
                }
            };
            final C24935 r0 = new ApiCallback<KeysUploadResponse>() {
                public void onSuccess(KeysUploadResponse keysUploadResponse) {
                    MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                        public void run() {
                            if (!MXCrypto.this.hasBeenReleased()) {
                                Log.m209d(MXCrypto.LOG_TAG, "###########################################################");
                                String access$000 = MXCrypto.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("uploadDeviceKeys done for ");
                                sb.append(MXCrypto.this.mSession.getMyUserId());
                                Log.m209d(access$000, sb.toString());
                                String access$0002 = MXCrypto.LOG_TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("  - device id  : ");
                                sb2.append(MXCrypto.this.mSession.getCredentials().deviceId);
                                Log.m209d(access$0002, sb2.toString());
                                String access$0003 = MXCrypto.LOG_TAG;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("  - ed25519    : ");
                                sb3.append(MXCrypto.this.mOlmDevice.getDeviceEd25519Key());
                                Log.m209d(access$0003, sb3.toString());
                                String access$0004 = MXCrypto.LOG_TAG;
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("  - curve25519 : ");
                                sb4.append(MXCrypto.this.mOlmDevice.getDeviceCurve25519Key());
                                Log.m209d(access$0004, sb4.toString());
                                String access$0005 = MXCrypto.LOG_TAG;
                                StringBuilder sb5 = new StringBuilder();
                                sb5.append("  - oneTimeKeys: ");
                                sb5.append(MXCrypto.this.mLastPublishedOneTimeKeys);
                                Log.m209d(access$0005, sb5.toString());
                                Log.m209d(MXCrypto.LOG_TAG, "");
                                MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                                    public void run() {
                                        MXCrypto.this.maybeUploadOneTimeKeys(r4);
                                    }
                                });
                            }
                        }
                    });
                }

                public void onNetworkError(Exception exc) {
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## start failed : ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                    MXCrypto.this.onError(z);
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## start failed : ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$000, sb.toString());
                    MXCrypto.this.onError(z);
                }

                public void onUnexpectedError(Exception exc) {
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## start failed : ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                    MXCrypto.this.onError(z);
                }
            };
            if (this.mNetworkConnectivityReceiver == null || this.mNetworkConnectivityReceiver.isConnected()) {
                this.mIsStarting = true;
                getEncryptingThreadHandler().post(new Runnable() {
                    public void run() {
                        MXCrypto.this.uploadDeviceKeys(r0);
                    }
                });
                return;
            }
            this.mNetworkConnectivityReceiver.removeEventListener(this.mNetworkListener);
            this.mNetworkConnectivityReceiver.addEventListener(this.mNetworkListener);
        }
    }

    public void close() {
        if (this.mEncryptingHandlerThread != null) {
            this.mSession.getDataHandler().removeListener(this.mEventListener);
            getEncryptingThreadHandler().post(new Runnable() {
                public void run() {
                    if (MXCrypto.this.mOlmDevice != null) {
                        MXCrypto.this.mOlmDevice.release();
                        MXCrypto.this.mOlmDevice = null;
                    }
                    MXCrypto.this.mMyDevice = null;
                    MXCrypto.this.mCryptoStore.close();
                    MXCrypto.this.mCryptoStore = null;
                    if (MXCrypto.this.mEncryptingHandlerThread != null) {
                        MXCrypto.this.mEncryptingHandlerThread.quit();
                        MXCrypto.this.mEncryptingHandlerThread = null;
                    }
                    MXCrypto.this.mOutgoingRoomKeyRequestManager.stop();
                }
            });
            getDecryptingThreadHandler().post(new Runnable() {
                public void run() {
                    if (MXCrypto.this.mDecryptingHandlerThread != null) {
                        MXCrypto.this.mDecryptingHandlerThread.quit();
                        MXCrypto.this.mDecryptingHandlerThread = null;
                    }
                }
            });
        }
    }

    public MXOlmDevice getOlmDevice() {
        return this.mOlmDevice;
    }

    public void onSyncCompleted(final SyncResponse syncResponse, String str, final boolean z) {
        getEncryptingThreadHandler().post(new Runnable() {
            public void run() {
                if (syncResponse.deviceLists != null) {
                    MXCrypto.this.getDeviceList().handleDeviceListsChanges(syncResponse.deviceLists.changed, syncResponse.deviceLists.left);
                }
                if (syncResponse.deviceOneTimeKeysCount != null) {
                    MXCrypto.this.updateOneTimeKeyCount(syncResponse.deviceOneTimeKeysCount.signed_curve25519 != null ? syncResponse.deviceOneTimeKeysCount.signed_curve25519.intValue() : 0);
                }
                if (MXCrypto.this.isStarted()) {
                    MXCrypto.this.mDevicesList.refreshOutdatedDeviceLists();
                }
                if (!z && MXCrypto.this.isStarted()) {
                    MXCrypto.this.maybeUploadOneTimeKeys();
                    MXCrypto.this.processReceivedRoomKeyRequests();
                }
            }
        });
    }

    public void getUserDevices(final String str, final ApiCallback<List<MXDeviceInfo>> apiCallback) {
        getEncryptingThreadHandler().post(new Runnable() {
            public void run() {
                final List userDevices = MXCrypto.this.getUserDevices(str);
                if (apiCallback != null) {
                    MXCrypto.this.getUIHandler().post(new Runnable() {
                        public void run() {
                            apiCallback.onSuccess(userDevices);
                        }
                    });
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateOneTimeKeyCount(int i) {
        this.mOneTimeKeyCount = Integer.valueOf(i);
    }

    public MXDeviceInfo deviceWithIdentityKey(String str, String str2, String str3) {
        MXDeviceInfo mXDeviceInfo = null;
        if (hasBeenReleased() || ((!TextUtils.equals(str3, MXCryptoAlgorithms.MXCRYPTO_ALGORITHM_MEGOLM) && !TextUtils.equals(str3, MXCryptoAlgorithms.MXCRYPTO_ALGORITHM_OLM)) || TextUtils.isEmpty(str2))) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Handler decryptingThreadHandler = getDecryptingThreadHandler();
        final String str4 = str2;
        final String str5 = str;
        final ArrayList arrayList2 = arrayList;
        final CountDownLatch countDownLatch2 = countDownLatch;
        C242011 r2 = new Runnable() {
            public void run() {
                List<MXDeviceInfo> userDevices = MXCrypto.this.getUserDevices(str4);
                if (userDevices != null) {
                    for (MXDeviceInfo mXDeviceInfo : userDevices) {
                        for (String str : mXDeviceInfo.keys.keySet()) {
                            if (str.startsWith("curve25519:") && TextUtils.equals(str5, (CharSequence) mXDeviceInfo.keys.get(str))) {
                                arrayList2.add(mXDeviceInfo);
                            }
                        }
                    }
                }
                countDownLatch2.countDown();
            }
        };
        decryptingThreadHandler.post(r2);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            String str6 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## deviceWithIdentityKey() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str6, sb.toString());
        }
        if (arrayList.size() > 0) {
            mXDeviceInfo = (MXDeviceInfo) arrayList.get(0);
        }
        return mXDeviceInfo;
    }

    public void getDeviceInfo(final String str, final String str2, final ApiCallback<MXDeviceInfo> apiCallback) {
        getDecryptingThreadHandler().post(new Runnable() {
            public void run() {
                final MXDeviceInfo userDevice = (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) ? null : MXCrypto.this.mCryptoStore.getUserDevice(str2, str);
                if (apiCallback != null) {
                    MXCrypto.this.getUIHandler().post(new Runnable() {
                        public void run() {
                            apiCallback.onSuccess(userDevice);
                        }
                    });
                }
            }
        });
    }

    public void setDevicesKnown(final List<MXDeviceInfo> list, final ApiCallback<Void> apiCallback) {
        if (!hasBeenReleased()) {
            getEncryptingThreadHandler().post(new Runnable() {
                public void run() {
                    HashMap hashMap = new HashMap();
                    for (MXDeviceInfo mXDeviceInfo : list) {
                        List list = (List) hashMap.get(mXDeviceInfo.userId);
                        if (list == null) {
                            list = new ArrayList();
                            hashMap.put(mXDeviceInfo.userId, list);
                        }
                        list.add(mXDeviceInfo.deviceId);
                    }
                    for (String str : hashMap.keySet()) {
                        Map userDevices = MXCrypto.this.mCryptoStore.getUserDevices(str);
                        if (userDevices != null) {
                            boolean z = false;
                            for (String str2 : (List) hashMap.get(str)) {
                                MXDeviceInfo mXDeviceInfo2 = (MXDeviceInfo) userDevices.get(str2);
                                if (mXDeviceInfo2 != null && mXDeviceInfo2.isUnknown()) {
                                    mXDeviceInfo2.mVerified = 0;
                                    z = true;
                                }
                            }
                            if (z) {
                                MXCrypto.this.mCryptoStore.storeUserDevices(str, userDevices);
                            }
                        }
                    }
                    if (apiCallback != null) {
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                apiCallback.onSuccess(null);
                            }
                        });
                    }
                }
            });
        }
    }

    public void setDeviceVerification(int i, String str, String str2, ApiCallback<Void> apiCallback) {
        if (!hasBeenReleased()) {
            Handler encryptingThreadHandler = getEncryptingThreadHandler();
            final String str3 = str;
            final String str4 = str2;
            final ApiCallback<Void> apiCallback2 = apiCallback;
            final int i2 = i;
            C242514 r1 = new Runnable() {
                public void run() {
                    MXDeviceInfo userDevice = MXCrypto.this.mCryptoStore.getUserDevice(str3, str4);
                    if (userDevice == null) {
                        String access$000 = MXCrypto.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## setDeviceVerification() : Unknown device ");
                        sb.append(str4);
                        sb.append(":");
                        sb.append(str3);
                        Log.m211e(access$000, sb.toString());
                        if (apiCallback2 != null) {
                            MXCrypto.this.getUIHandler().post(new Runnable() {
                                public void run() {
                                    apiCallback2.onSuccess(null);
                                }
                            });
                        }
                        return;
                    }
                    if (userDevice.mVerified != i2) {
                        userDevice.mVerified = i2;
                        MXCrypto.this.mCryptoStore.storeUserDevice(str4, userDevice);
                    }
                    if (apiCallback2 != null) {
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                apiCallback2.onSuccess(null);
                            }
                        });
                    }
                }
            };
            encryptingThreadHandler.post(r1);
        }
    }

    /* access modifiers changed from: private */
    public boolean setEncryptionInRoom(String str, String str2, boolean z) {
        Collection<RoomMember> collection;
        boolean z2 = false;
        if (hasBeenReleased()) {
            return false;
        }
        String roomAlgorithm = this.mCryptoStore.getRoomAlgorithm(str);
        if (TextUtils.isEmpty(roomAlgorithm) || TextUtils.equals(roomAlgorithm, str2)) {
            Class encryptorClassForAlgorithm = MXCryptoAlgorithms.sharedAlgorithms().encryptorClassForAlgorithm(str2);
            if (encryptorClassForAlgorithm == null) {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## setEncryptionInRoom() : Unable to encrypt with ");
                sb.append(str2);
                Log.m211e(str3, sb.toString());
                return false;
            }
            this.mCryptoStore.storeRoomAlgorithm(str, str2);
            try {
                IMXEncrypting iMXEncrypting = (IMXEncrypting) encryptorClassForAlgorithm.getConstructors()[0].newInstance(new Object[0]);
                iMXEncrypting.initWithMatrixSession(this.mSession, str);
                synchronized (this.mRoomEncryptors) {
                    this.mRoomEncryptors.put(str, iMXEncrypting);
                }
                if (roomAlgorithm == null) {
                    String str4 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Enabling encryption in ");
                    sb2.append(str);
                    sb2.append(" for the first time; invalidating device lists for all users therein");
                    Log.m209d(str4, sb2.toString());
                    Room room = this.mSession.getDataHandler().getRoom(str);
                    if (room != null) {
                        if (this.mCryptoConfig.mEnableEncryptionForInvitedMembers && room.shouldEncryptForInvitedMembers()) {
                            z2 = true;
                        }
                        if (z2) {
                            collection = room.getActiveMembers();
                        } else {
                            collection = room.getJoinedMembers();
                        }
                        ArrayList arrayList = new ArrayList();
                        for (RoomMember userId : collection) {
                            arrayList.add(userId.getUserId());
                        }
                        getDeviceList().startTrackingDeviceList(arrayList);
                        if (!z) {
                            getDeviceList().refreshOutdatedDeviceLists();
                        }
                    }
                }
                return true;
            } catch (Exception unused) {
                Log.m211e(LOG_TAG, "## setEncryptionInRoom() : fail to load the class");
                return false;
            }
        } else {
            String str5 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## setEncryptionInRoom() : Ignoring m.room.encryption event which requests a change of config in ");
            sb3.append(str);
            Log.m211e(str5, sb3.toString());
            return false;
        }
    }

    public boolean isRoomEncrypted(String str) {
        boolean containsKey;
        if (str == null) {
            return false;
        }
        synchronized (this.mRoomEncryptors) {
            containsKey = this.mRoomEncryptors.containsKey(str);
            if (!containsKey) {
                Room room = this.mSession.getDataHandler().getRoom(str);
                if (room != null) {
                    containsKey = room.getState().isEncrypted();
                }
            }
        }
        return containsKey;
    }

    public List<MXDeviceInfo> getUserDevices(String str) {
        Map userDevices = getCryptoStore().getUserDevices(str);
        return userDevices != null ? new ArrayList(userDevices.values()) : new ArrayList();
    }

    public void ensureOlmSessionsForUsers(List<String> list, ApiCallback<MXUsersDevicesMap<MXOlmSessionResult>> apiCallback) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## ensureOlmSessionsForUsers() : ensureOlmSessionsForUsers ");
        sb.append(list);
        Log.m209d(str, sb.toString());
        HashMap hashMap = new HashMap();
        for (String str2 : list) {
            hashMap.put(str2, new ArrayList());
            for (MXDeviceInfo mXDeviceInfo : getUserDevices(str2)) {
                if (!TextUtils.equals(mXDeviceInfo.identityKey(), this.mOlmDevice.getDeviceCurve25519Key()) && !mXDeviceInfo.isVerified()) {
                    ((ArrayList) hashMap.get(str2)).add(mXDeviceInfo);
                }
            }
        }
        ensureOlmSessionsForDevices(hashMap, apiCallback);
    }

    public void ensureOlmSessionsForDevices(final HashMap<String, ArrayList<MXDeviceInfo>> hashMap, final ApiCallback<MXUsersDevicesMap<MXOlmSessionResult>> apiCallback) {
        ArrayList arrayList = new ArrayList();
        final MXUsersDevicesMap mXUsersDevicesMap = new MXUsersDevicesMap();
        for (String str : hashMap.keySet()) {
            Iterator it = ((ArrayList) hashMap.get(str)).iterator();
            while (it.hasNext()) {
                MXDeviceInfo mXDeviceInfo = (MXDeviceInfo) it.next();
                String str2 = mXDeviceInfo.deviceId;
                String sessionId = this.mOlmDevice.getSessionId(mXDeviceInfo.identityKey());
                if (TextUtils.isEmpty(sessionId)) {
                    arrayList.add(mXDeviceInfo);
                }
                mXUsersDevicesMap.setObject(new MXOlmSessionResult(mXDeviceInfo, sessionId), str, str2);
            }
        }
        if (arrayList.size() == 0 || !Matrix.getInstance(VectorApp.getInstance()).isConnected()) {
            if (apiCallback != null) {
                getUIHandler().post(new Runnable() {
                    public void run() {
                        apiCallback.onSuccess(mXUsersDevicesMap);
                    }
                });
            }
            return;
        }
        MXUsersDevicesMap mXUsersDevicesMap2 = new MXUsersDevicesMap();
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            MXDeviceInfo mXDeviceInfo2 = (MXDeviceInfo) it2.next();
            mXUsersDevicesMap2.setObject(MXKey.KEY_SIGNED_CURVE_25519_TYPE, mXDeviceInfo2.userId, mXDeviceInfo2.deviceId);
        }
        String str3 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## claimOneTimeKeysForUsersDevices() : ");
        sb.append(mXUsersDevicesMap2);
        Log.m209d(str3, sb.toString());
        this.mSession.getCryptoRestClient().claimOneTimeKeysForUsersDevices(mXUsersDevicesMap2, new ApiCallback<MXUsersDevicesMap<MXKey>>() {
            public void onSuccess(final MXUsersDevicesMap<MXKey> mXUsersDevicesMap) {
                MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                    public void run() {
                        try {
                            String access$000 = MXCrypto.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## claimOneTimeKeysForUsersDevices() : keysClaimResponse.oneTimeKeys: ");
                            sb.append(mXUsersDevicesMap);
                            Log.m209d(access$000, sb.toString());
                            for (String str : hashMap.keySet()) {
                                Iterator it = ((ArrayList) hashMap.get(str)).iterator();
                                while (it.hasNext()) {
                                    MXDeviceInfo mXDeviceInfo = (MXDeviceInfo) it.next();
                                    MXKey mXKey = null;
                                    List<String> userDeviceIds = mXUsersDevicesMap.getUserDeviceIds(str);
                                    if (userDeviceIds != null) {
                                        for (String str2 : userDeviceIds) {
                                            MXOlmSessionResult mXOlmSessionResult = (MXOlmSessionResult) mXUsersDevicesMap.getObject(str2, str);
                                            if (mXOlmSessionResult.mSessionId == null) {
                                                MXKey mXKey2 = (MXKey) mXUsersDevicesMap.getObject(str2, str);
                                                if (TextUtils.equals(mXKey2.type, MXKey.KEY_SIGNED_CURVE_25519_TYPE)) {
                                                    mXKey = mXKey2;
                                                }
                                                if (mXKey == null) {
                                                    String access$0002 = MXCrypto.LOG_TAG;
                                                    StringBuilder sb2 = new StringBuilder();
                                                    sb2.append("## ensureOlmSessionsForDevices() : No one-time keys signed_curve25519 for device ");
                                                    sb2.append(str);
                                                    sb2.append(" : ");
                                                    sb2.append(str2);
                                                    Log.m209d(access$0002, sb2.toString());
                                                } else {
                                                    mXOlmSessionResult.mSessionId = MXCrypto.this.verifyKeyAndStartSession(mXKey, str, mXDeviceInfo);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            String access$0003 = MXCrypto.LOG_TAG;
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("## ensureOlmSessionsForDevices() ");
                            sb3.append(e.getMessage());
                            Log.m211e(access$0003, sb3.toString());
                        }
                        if (!MXCrypto.this.hasBeenReleased() && apiCallback != null) {
                            MXCrypto.this.getUIHandler().post(new Runnable() {
                                public void run() {
                                    apiCallback.onSuccess(mXUsersDevicesMap);
                                }
                            });
                        }
                    }
                });
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXCrypto.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## ensureOlmSessionsForUsers(): claimOneTimeKeysForUsersDevices request failed");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback != null) {
                    apiCallback.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXCrypto.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## ensureOlmSessionsForUsers(): claimOneTimeKeysForUsersDevices request failed");
                sb.append(matrixError.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXCrypto.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## ensureOlmSessionsForUsers(): claimOneTimeKeysForUsersDevices request failed");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public String verifyKeyAndStartSession(MXKey mXKey, String str, MXDeviceInfo mXDeviceInfo) {
        String str2;
        String str3 = mXDeviceInfo.deviceId;
        StringBuilder sb = new StringBuilder();
        sb.append("ed25519:");
        sb.append(str3);
        String signatureForUserId = mXKey.signatureForUserId(str, sb.toString());
        String str4 = null;
        if (!TextUtils.isEmpty(signatureForUserId) && !TextUtils.isEmpty(mXDeviceInfo.fingerprint())) {
            boolean z = false;
            try {
                this.mOlmDevice.verifySignature(mXDeviceInfo.fingerprint(), mXKey.signalableJSONDictionary(), signatureForUserId);
                z = true;
                str2 = null;
            } catch (Exception e) {
                str2 = e.getMessage();
            }
            if (z) {
                str4 = getOlmDevice().createOutboundSession(mXDeviceInfo.identityKey(), mXKey.value);
                if (!TextUtils.isEmpty(str4)) {
                    String str5 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## verifyKeyAndStartSession() : Started new sessionid ");
                    sb2.append(str4);
                    sb2.append(" for device ");
                    sb2.append(mXDeviceInfo);
                    sb2.append("(theirOneTimeKey: ");
                    sb2.append(mXKey.value);
                    sb2.append(")");
                    Log.m209d(str5, sb2.toString());
                } else {
                    String str6 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## verifyKeyAndStartSession() : Error starting session with device ");
                    sb3.append(str);
                    sb3.append(":");
                    sb3.append(str3);
                    Log.m211e(str6, sb3.toString());
                }
            } else {
                String str7 = LOG_TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("## verifyKeyAndStartSession() : Unable to verify signature on one-time key for device ");
                sb4.append(str);
                sb4.append(":");
                sb4.append(str3);
                sb4.append(" Error ");
                sb4.append(str2);
                Log.m211e(str7, sb4.toString());
            }
        }
        return str4;
    }

    public void encryptEventContent(JsonElement jsonElement, String str, Room room, ApiCallback<MXEncryptEventContentResult> apiCallback) {
        Collection<RoomMember> collection;
        boolean z = false;
        if (isStarted() || !Matrix.getInstance(VectorApp.getInstance()).isConnected()) {
            final ArrayList arrayList = new ArrayList();
            if (this.mCryptoConfig.mEnableEncryptionForInvitedMembers && room.shouldEncryptForInvitedMembers()) {
                z = true;
            }
            if (z) {
                collection = room.getActiveMembers();
            } else {
                collection = room.getJoinedMembers();
            }
            for (RoomMember userId : collection) {
                arrayList.add(userId.getUserId());
            }
            Handler encryptingThreadHandler = getEncryptingThreadHandler();
            final Room room2 = room;
            final JsonElement jsonElement2 = jsonElement;
            final String str2 = str;
            final ApiCallback<MXEncryptEventContentResult> apiCallback2 = apiCallback;
            C243318 r2 = new Runnable() {
                public void run() {
                    IMXEncrypting iMXEncrypting;
                    synchronized (MXCrypto.this.mRoomEncryptors) {
                        iMXEncrypting = (IMXEncrypting) MXCrypto.this.mRoomEncryptors.get(room2.getRoomId());
                    }
                    if (iMXEncrypting == null) {
                        String encryptionAlgorithm = room2.getState().encryptionAlgorithm();
                        if (encryptionAlgorithm != null && MXCrypto.this.setEncryptionInRoom(room2.getRoomId(), encryptionAlgorithm, false)) {
                            synchronized (MXCrypto.this.mRoomEncryptors) {
                                iMXEncrypting = (IMXEncrypting) MXCrypto.this.mRoomEncryptors.get(room2.getRoomId());
                            }
                        }
                    }
                    if (iMXEncrypting != null) {
                        final long currentTimeMillis = System.currentTimeMillis();
                        Log.m209d(MXCrypto.LOG_TAG, "## encryptEventContent() starts");
                        iMXEncrypting.encryptEventContent(jsonElement2, str2, arrayList, new ApiCallback<JsonElement>() {
                            public void onSuccess(JsonElement jsonElement) {
                                String access$000 = MXCrypto.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## encryptEventContent() : succeeds after ");
                                sb.append(System.currentTimeMillis() - currentTimeMillis);
                                sb.append(" ms");
                                Log.m209d(access$000, sb.toString());
                                if (apiCallback2 != null) {
                                    apiCallback2.onSuccess(new MXEncryptEventContentResult(jsonElement, Event.EVENT_TYPE_MESSAGE_ENCRYPTED));
                                }
                            }

                            public void onNetworkError(Exception exc) {
                                String access$000 = MXCrypto.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## encryptEventContent() : onNetworkError ");
                                sb.append(exc.getMessage());
                                Log.m211e(access$000, sb.toString());
                                if (apiCallback2 != null) {
                                    apiCallback2.onNetworkError(exc);
                                }
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                String access$000 = MXCrypto.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## encryptEventContent() : onMatrixError ");
                                sb.append(matrixError.getMessage());
                                Log.m211e(access$000, sb.toString());
                                if (apiCallback2 != null) {
                                    apiCallback2.onMatrixError(matrixError);
                                }
                            }

                            public void onUnexpectedError(Exception exc) {
                                String access$000 = MXCrypto.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## encryptEventContent() : onUnexpectedError ");
                                sb.append(exc.getMessage());
                                Log.m211e(access$000, sb.toString());
                                if (apiCallback2 != null) {
                                    apiCallback2.onUnexpectedError(exc);
                                }
                            }
                        });
                        return;
                    }
                    String encryptionAlgorithm2 = room2.getState().encryptionAlgorithm();
                    String str = MXCryptoError.UNABLE_TO_ENCRYPT_REASON;
                    Object[] objArr = new Object[1];
                    if (encryptionAlgorithm2 == null) {
                        encryptionAlgorithm2 = MXCryptoError.NO_MORE_ALGORITHM_REASON;
                    }
                    objArr[0] = encryptionAlgorithm2;
                    final String format = String.format(str, objArr);
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## encryptEventContent() : ");
                    sb.append(format);
                    Log.m211e(access$000, sb.toString());
                    if (apiCallback2 != null) {
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                apiCallback2.onMatrixError(new MXCryptoError(MXCryptoError.UNABLE_TO_ENCRYPT_ERROR_CODE, MXCryptoError.UNABLE_TO_ENCRYPT, format));
                            }
                        });
                    }
                }
            };
            encryptingThreadHandler.post(r2);
            return;
        }
        Log.m209d(LOG_TAG, "## encryptEventContent() : wait after e2e init");
        final JsonElement jsonElement3 = jsonElement;
        final String str3 = str;
        final Room room3 = room;
        final ApiCallback<MXEncryptEventContentResult> apiCallback3 = apiCallback;
        C243217 r3 = new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                MXCrypto.this.encryptEventContent(jsonElement3, str3, room3, apiCallback3);
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXCrypto.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## encryptEventContent() : onNetworkError while waiting to start e2e : ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback3 != null) {
                    apiCallback3.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXCrypto.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## encryptEventContent() : onMatrixError while waiting to start e2e : ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback3 != null) {
                    apiCallback3.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXCrypto.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## encryptEventContent() : onUnexpectedError while waiting to start e2e : ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback3 != null) {
                    apiCallback3.onUnexpectedError(exc);
                }
            }
        };
        start(false, r3);
    }

    public MXEventDecryptionResult decryptEvent(Event event, String str) throws MXDecryptionException {
        if (event == null) {
            Log.m211e(LOG_TAG, "## decryptEvent : null event");
            return null;
        }
        final EventContent wireEventContent = event.getWireEventContent();
        if (wireEventContent == null) {
            Log.m211e(LOG_TAG, "## decryptEvent : empty event content");
            return null;
        }
        ArrayList arrayList = new ArrayList();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ArrayList arrayList2 = new ArrayList();
        Handler decryptingThreadHandler = getDecryptingThreadHandler();
        final Event event2 = event;
        final ArrayList arrayList3 = arrayList2;
        final String str2 = str;
        final ArrayList arrayList4 = arrayList;
        final CountDownLatch countDownLatch2 = countDownLatch;
        C243619 r2 = new Runnable() {
            public void run() {
                MXEventDecryptionResult mXEventDecryptionResult;
                IMXDecrypting access$2600 = MXCrypto.this.getRoomDecryptor(event2.roomId, wireEventContent.algorithm);
                if (access$2600 == null) {
                    String format = String.format(MXCryptoError.UNABLE_TO_DECRYPT_REASON, new Object[]{event2.eventId, wireEventContent.algorithm});
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## decryptEvent() : ");
                    sb.append(format);
                    Log.m211e(access$000, sb.toString());
                    arrayList3.add(new MXDecryptionException(new MXCryptoError(MXCryptoError.UNABLE_TO_DECRYPT_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, format)));
                } else {
                    try {
                        mXEventDecryptionResult = access$2600.decryptEvent(event2, str2);
                    } catch (MXDecryptionException e) {
                        arrayList3.add(e);
                        mXEventDecryptionResult = null;
                    }
                    if (mXEventDecryptionResult != null) {
                        arrayList4.add(mXEventDecryptionResult);
                    }
                }
                countDownLatch2.countDown();
            }
        };
        decryptingThreadHandler.post(r2);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## decryptEvent() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str3, sb.toString());
        }
        if (!arrayList2.isEmpty()) {
            throw ((MXDecryptionException) arrayList2.get(0));
        } else if (!arrayList.isEmpty()) {
            return (MXEventDecryptionResult) arrayList.get(0);
        } else {
            return null;
        }
    }

    public void resetReplayAttackCheckInTimeline(final String str) {
        if (str != null && getOlmDevice() != null) {
            getDecryptingThreadHandler().post(new Runnable() {
                public void run() {
                    MXCrypto.this.getOlmDevice().resetReplayAttackCheckInTimeline(str);
                }
            });
        }
    }

    public Map<String, Object> encryptMessage(Map<String, Object> map, List<MXDeviceInfo> list) {
        if (hasBeenReleased()) {
            return new HashMap();
        }
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        for (MXDeviceInfo mXDeviceInfo : list) {
            arrayList.add(mXDeviceInfo.identityKey());
            hashMap.put(mXDeviceInfo.identityKey(), mXDeviceInfo);
        }
        HashMap hashMap2 = new HashMap(map);
        hashMap2.put(BingRule.KIND_SENDER, this.mSession.getMyUserId());
        hashMap2.put("sender_device", this.mSession.getCredentials().deviceId);
        HashMap hashMap3 = new HashMap();
        hashMap3.put(OlmAccount.JSON_KEY_FINGER_PRINT_KEY, this.mOlmDevice.getDeviceEd25519Key());
        hashMap2.put("keys", hashMap3);
        HashMap hashMap4 = new HashMap();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            String sessionId = this.mOlmDevice.getSessionId(str);
            if (!TextUtils.isEmpty(sessionId)) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Using sessionid ");
                sb.append(sessionId);
                sb.append(" for device ");
                sb.append(str);
                Log.m209d(str2, sb.toString());
                MXDeviceInfo mXDeviceInfo2 = (MXDeviceInfo) hashMap.get(str);
                hashMap2.put("recipient", mXDeviceInfo2.userId);
                HashMap hashMap5 = new HashMap();
                hashMap5.put(OlmAccount.JSON_KEY_FINGER_PRINT_KEY, mXDeviceInfo2.fingerprint());
                hashMap2.put("recipient_keys", hashMap5);
                hashMap4.put(str, this.mOlmDevice.encryptMessage(str, sessionId, JsonUtils.convertToUTF8(JsonUtils.canonicalize(JsonUtils.getGson(false).toJsonTree(hashMap2)).toString())));
            }
        }
        HashMap hashMap6 = new HashMap();
        hashMap6.put("algorithm", MXCryptoAlgorithms.MXCRYPTO_ALGORITHM_OLM);
        hashMap6.put("sender_key", this.mOlmDevice.getDeviceCurve25519Key());
        hashMap6.put("ciphertext", hashMap4);
        return hashMap6;
    }

    private List<Room> getE2eRooms() {
        ArrayList arrayList = new ArrayList();
        if (this.mSession.getDataHandler() == null || this.mSession.getDataHandler().getStore() == null) {
            return arrayList;
        }
        for (Room room : new ArrayList(this.mSession.getDataHandler().getStore().getRooms())) {
            if (room.isEncrypted()) {
                RoomMember member = room.getMember(this.mSession.getMyUserId());
                if (member != null) {
                    String str = member.membership;
                    if (TextUtils.equals(str, RoomMember.MEMBERSHIP_JOIN) || TextUtils.equals(str, "invite")) {
                        arrayList.add(room);
                    }
                }
            }
        }
        return arrayList;
    }

    private List<String> getE2eRoomMembers() {
        HashSet hashSet = new HashSet();
        for (Room activeMembers : getE2eRooms()) {
            for (RoomMember roomMember : activeMembers.getActiveMembers()) {
                if (MXSession.PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER.matcher(roomMember.getUserId()).matches()) {
                    hashSet.add(roomMember.getUserId());
                }
            }
        }
        return new ArrayList(hashSet);
    }

    /* access modifiers changed from: private */
    public void onToDeviceEvent(final Event event) {
        if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_ROOM_KEY) || TextUtils.equals(event.getType(), Event.EVENT_TYPE_FORWARDED_ROOM_KEY)) {
            getDecryptingThreadHandler().post(new Runnable() {
                public void run() {
                    MXCrypto.this.onRoomKeyEvent(event);
                }
            });
        } else if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_ROOM_KEY_REQUEST)) {
            getEncryptingThreadHandler().post(new Runnable() {
                public void run() {
                    MXCrypto.this.onRoomKeyRequestEvent(event);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onRoomKeyEvent(Event event) {
        if (event == null) {
            Log.m211e(LOG_TAG, "## onRoomKeyEvent() : null event");
            return;
        }
        RoomKeyContent roomKeyContent = JsonUtils.toRoomKeyContent(event.getContentAsJsonObject());
        String str = roomKeyContent.room_id;
        String str2 = roomKeyContent.algorithm;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            Log.m211e(LOG_TAG, "## onRoomKeyEvent() : missing fields");
            return;
        }
        IMXDecrypting roomDecryptor = getRoomDecryptor(str, str2);
        if (roomDecryptor == null) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRoomKeyEvent() : Unable to handle keys for ");
            sb.append(str2);
            Log.m211e(str3, sb.toString());
            return;
        }
        roomDecryptor.onRoomKeyEvent(event);
    }

    /* access modifiers changed from: private */
    public void onRoomKeyRequestEvent(Event event) {
        RoomKeyRequest roomKeyRequest = JsonUtils.toRoomKeyRequest(event.getContentAsJsonObject());
        if (roomKeyRequest.action != null) {
            String str = roomKeyRequest.action;
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != -524427085) {
                if (hashCode == 1095692943 && str.equals("request")) {
                    c = 0;
                }
            } else if (str.equals(RoomKeyRequest.ACTION_REQUEST_CANCELLATION)) {
                c = 1;
            }
            switch (c) {
                case 0:
                    synchronized (this.mReceivedRoomKeyRequests) {
                        this.mReceivedRoomKeyRequests.add(new IncomingRoomKeyRequest(event));
                    }
                    return;
                case 1:
                    synchronized (this.mReceivedRoomKeyRequestCancellations) {
                        this.mReceivedRoomKeyRequestCancellations.add(new IncomingRoomKeyRequestCancellation(event));
                    }
                    return;
                default:
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onRoomKeyRequestEvent() : unsupported action ");
                    sb.append(roomKeyRequest.action);
                    Log.m211e(str2, sb.toString());
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void processReceivedRoomKeyRequests() {
        ArrayList<IncomingRoomKeyRequestCancellation> arrayList;
        List<IncomingRoomKeyRequest> list;
        synchronized (this.mReceivedRoomKeyRequests) {
            arrayList = null;
            if (!this.mReceivedRoomKeyRequests.isEmpty()) {
                list = new ArrayList<>(this.mReceivedRoomKeyRequests);
                this.mReceivedRoomKeyRequests.clear();
            } else {
                list = null;
            }
        }
        if (list != null) {
            for (final IncomingRoomKeyRequest incomingRoomKeyRequest : list) {
                String str = incomingRoomKeyRequest.mUserId;
                String str2 = incomingRoomKeyRequest.mDeviceId;
                RoomKeyRequestBody roomKeyRequestBody = incomingRoomKeyRequest.mRequestBody;
                String str3 = roomKeyRequestBody.room_id;
                String str4 = roomKeyRequestBody.algorithm;
                String str5 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("m.room_key_request from ");
                sb.append(str);
                sb.append(":");
                sb.append(str2);
                sb.append(" for ");
                sb.append(str3);
                sb.append(" / ");
                sb.append(roomKeyRequestBody.session_id);
                sb.append(" id ");
                sb.append(incomingRoomKeyRequest.mRequestId);
                Log.m209d(str5, sb.toString());
                if (!TextUtils.equals(this.mSession.getMyUserId(), str)) {
                    Log.m211e(LOG_TAG, "## processReceivedRoomKeyRequests() : Ignoring room key request from other user for now");
                    return;
                }
                final IMXDecrypting roomDecryptor = getRoomDecryptor(str3, str4);
                if (roomDecryptor == null) {
                    String str6 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## processReceivedRoomKeyRequests() : room key request for unknown ");
                    sb2.append(str4);
                    sb2.append(" in room ");
                    sb2.append(str3);
                    Log.m211e(str6, sb2.toString());
                } else if (!roomDecryptor.hasKeysForKeyRequest(incomingRoomKeyRequest)) {
                    String str7 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## processReceivedRoomKeyRequests() : room key request for unknown session ");
                    sb3.append(roomKeyRequestBody.session_id);
                    Log.m211e(str7, sb3.toString());
                    this.mCryptoStore.deleteIncomingRoomKeyRequest(incomingRoomKeyRequest);
                } else if (!TextUtils.equals(str2, getMyDevice().deviceId) || !TextUtils.equals(this.mSession.getMyUserId(), str)) {
                    incomingRoomKeyRequest.mShare = new Runnable() {
                        public void run() {
                            MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                                public void run() {
                                    roomDecryptor.shareKeysWithDevice(incomingRoomKeyRequest);
                                    MXCrypto.this.mCryptoStore.deleteIncomingRoomKeyRequest(incomingRoomKeyRequest);
                                }
                            });
                        }
                    };
                    incomingRoomKeyRequest.mIgnore = new Runnable() {
                        public void run() {
                            MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                                public void run() {
                                    MXCrypto.this.mCryptoStore.deleteIncomingRoomKeyRequest(incomingRoomKeyRequest);
                                }
                            });
                        }
                    };
                    MXDeviceInfo userDevice = this.mCryptoStore.getUserDevice(str2, str);
                    if (userDevice != null) {
                        if (userDevice.isVerified()) {
                            Log.m209d(LOG_TAG, "## processReceivedRoomKeyRequests() : device is already verified: sharing keys");
                            this.mCryptoStore.deleteIncomingRoomKeyRequest(incomingRoomKeyRequest);
                            incomingRoomKeyRequest.mShare.run();
                        } else if (userDevice.isBlocked()) {
                            Log.m209d(LOG_TAG, "## processReceivedRoomKeyRequests() : device is blocked -> ignored");
                            this.mCryptoStore.deleteIncomingRoomKeyRequest(incomingRoomKeyRequest);
                        }
                    }
                    this.mCryptoStore.storeIncomingRoomKeyRequest(incomingRoomKeyRequest);
                    onRoomKeyRequest(incomingRoomKeyRequest);
                } else {
                    Log.m209d(LOG_TAG, "## processReceivedRoomKeyRequests() : oneself device - ignored");
                    this.mCryptoStore.deleteIncomingRoomKeyRequest(incomingRoomKeyRequest);
                }
            }
        }
        synchronized (this.mReceivedRoomKeyRequestCancellations) {
            if (!this.mReceivedRoomKeyRequestCancellations.isEmpty()) {
                arrayList = new ArrayList<>(this.mReceivedRoomKeyRequestCancellations);
                this.mReceivedRoomKeyRequestCancellations.clear();
            }
        }
        if (arrayList != null) {
            for (IncomingRoomKeyRequestCancellation incomingRoomKeyRequestCancellation : arrayList) {
                String str8 = LOG_TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("## ## processReceivedRoomKeyRequests() : m.room_key_request cancellation for ");
                sb4.append(incomingRoomKeyRequestCancellation.mUserId);
                sb4.append(":");
                sb4.append(incomingRoomKeyRequestCancellation.mDeviceId);
                sb4.append(" id ");
                sb4.append(incomingRoomKeyRequestCancellation.mRequestId);
                Log.m209d(str8, sb4.toString());
                onRoomKeyRequestCancellation(incomingRoomKeyRequestCancellation);
                this.mCryptoStore.deleteIncomingRoomKeyRequest(incomingRoomKeyRequestCancellation);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onCryptoEvent(final Event event) {
        final EventContent wireEventContent = event.getWireEventContent();
        getEncryptingThreadHandler().post(new Runnable() {
            public void run() {
                MXCrypto.this.setEncryptionInRoom(event.roomId, wireEventContent.algorithm, true);
            }
        });
    }

    /* access modifiers changed from: private */
    public void onRoomMembership(Event event) {
        IMXEncrypting iMXEncrypting;
        synchronized (this.mRoomEncryptors) {
            iMXEncrypting = (IMXEncrypting) this.mRoomEncryptors.get(event.roomId);
        }
        if (iMXEncrypting != null) {
            final String str = event.stateKey;
            final Room room = this.mSession.getDataHandler().getRoom(event.roomId);
            RoomMember member = room.getState().getMember(str);
            if (member != null) {
                final String str2 = member.membership;
                getEncryptingThreadHandler().post(new Runnable() {
                    public void run() {
                        if (TextUtils.equals(str2, RoomMember.MEMBERSHIP_JOIN)) {
                            MXCrypto.this.getDeviceList().startTrackingDeviceList(Arrays.asList(new String[]{str}));
                        } else if (TextUtils.equals(str2, "invite") && room.shouldEncryptForInvitedMembers() && MXCrypto.this.mCryptoConfig.mEnableEncryptionForInvitedMembers) {
                            MXCrypto.this.getDeviceList().startTrackingDeviceList(Arrays.asList(new String[]{str}));
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void uploadDeviceKeys(ApiCallback<KeysUploadResponse> apiCallback) {
        String signJSON = this.mOlmDevice.signJSON(this.mMyDevice.signalableJSONDictionary());
        HashMap hashMap = new HashMap();
        StringBuilder sb = new StringBuilder();
        sb.append("ed25519:");
        sb.append(this.mMyDevice.deviceId);
        hashMap.put(sb.toString(), signJSON);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(this.mSession.getMyUserId(), hashMap);
        this.mMyDevice.signatures = hashMap2;
        this.mSession.getCryptoRestClient().uploadKeys(this.mMyDevice.JSONDictionary(), null, this.mMyDevice.deviceId, apiCallback);
    }

    /* access modifiers changed from: private */
    public void uploadLoop(int i, final int i2, final ApiCallback<Void> apiCallback) {
        if (i2 <= i) {
            if (apiCallback != null) {
                getUIHandler().post(new Runnable() {
                    public void run() {
                        apiCallback.onSuccess(null);
                    }
                });
            }
            return;
        }
        getOlmDevice().generateOneTimeKeys(Math.min(i2 - i, 5));
        uploadOneTimeKeys(new SimpleApiCallback<KeysUploadResponse>(apiCallback) {
            public void onSuccess(final KeysUploadResponse keysUploadResponse) {
                MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                    public void run() {
                        if (keysUploadResponse.hasOneTimeKeyCountsForAlgorithm(MXKey.KEY_SIGNED_CURVE_25519_TYPE)) {
                            MXCrypto.this.uploadLoop(keysUploadResponse.oneTimeKeyCountsForAlgorithm(MXKey.KEY_SIGNED_CURVE_25519_TYPE), i2, apiCallback);
                            return;
                        }
                        Log.m211e(MXCrypto.LOG_TAG, "## uploadLoop() : response for uploading keys does not contain one_time_key_counts.signed_curve25519");
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                apiCallback.onUnexpectedError(new Exception("response for uploading keys does not contain one_time_key_counts.signed_curve25519"));
                            }
                        });
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void maybeUploadOneTimeKeys() {
        maybeUploadOneTimeKeys(null);
    }

    /* access modifiers changed from: private */
    public void maybeUploadOneTimeKeys(final ApiCallback<Void> apiCallback) {
        if (this.mOneTimeKeyCheckInProgress) {
            getUIHandler().post(new Runnable() {
                public void run() {
                    if (apiCallback != null) {
                        apiCallback.onSuccess(null);
                    }
                }
            });
        } else if (System.currentTimeMillis() - this.mLastOneTimeKeyCheck < 60000) {
            getUIHandler().post(new Runnable() {
                public void run() {
                    if (apiCallback != null) {
                        apiCallback.onSuccess(null);
                    }
                }
            });
        } else {
            this.mLastOneTimeKeyCheck = System.currentTimeMillis();
            this.mOneTimeKeyCheckInProgress = true;
            final int floor = (int) Math.floor(((double) getOlmDevice().getMaxNumberOfOneTimeKeys()) / 2.0d);
            if (this.mOneTimeKeyCount != null) {
                uploadOTK(this.mOneTimeKeyCount.intValue(), floor, apiCallback);
            } else {
                this.mSession.getCryptoRestClient().uploadKeys(null, null, this.mMyDevice.deviceId, new ApiCallback<KeysUploadResponse>() {
                    private void onFailed(String str) {
                        if (str != null) {
                            String access$000 = MXCrypto.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## uploadKeys() : failed ");
                            sb.append(str);
                            Log.m211e(access$000, sb.toString());
                        }
                        MXCrypto.this.mOneTimeKeyCount = null;
                        MXCrypto.this.mOneTimeKeyCheckInProgress = false;
                    }

                    public void onSuccess(final KeysUploadResponse keysUploadResponse) {
                        MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                            public void run() {
                                if (!MXCrypto.this.hasBeenReleased()) {
                                    MXCrypto.this.uploadOTK(keysUploadResponse.oneTimeKeyCountsForAlgorithm(MXKey.KEY_SIGNED_CURVE_25519_TYPE), floor, apiCallback);
                                }
                            }
                        });
                    }

                    public void onNetworkError(final Exception exc) {
                        onFailed(exc.getMessage());
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                if (apiCallback != null) {
                                    apiCallback.onNetworkError(exc);
                                }
                            }
                        });
                    }

                    public void onMatrixError(final MatrixError matrixError) {
                        onFailed(matrixError.getMessage());
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                if (apiCallback != null) {
                                    apiCallback.onMatrixError(matrixError);
                                }
                            }
                        });
                    }

                    public void onUnexpectedError(final Exception exc) {
                        onFailed(exc.getMessage());
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                if (apiCallback != null) {
                                    apiCallback.onUnexpectedError(exc);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public void uploadOTK(int i, int i2, final ApiCallback<Void> apiCallback) {
        uploadLoop(i, i2, new ApiCallback<Void>() {
            private void uploadKeysDone(String str) {
                if (str != null) {
                    String access$000 = MXCrypto.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## maybeUploadOneTimeKeys() : failed ");
                    sb.append(str);
                    Log.m211e(access$000, sb.toString());
                }
                MXCrypto.this.mOneTimeKeyCount = null;
                MXCrypto.this.mOneTimeKeyCheckInProgress = false;
            }

            public void onSuccess(Void voidR) {
                Log.m209d(MXCrypto.LOG_TAG, "## maybeUploadOneTimeKeys() : succeeded");
                uploadKeysDone(null);
                MXCrypto.this.getUIHandler().post(new Runnable() {
                    public void run() {
                        if (apiCallback != null) {
                            apiCallback.onSuccess(null);
                        }
                    }
                });
            }

            public void onNetworkError(final Exception exc) {
                uploadKeysDone(exc.getMessage());
                MXCrypto.this.getUIHandler().post(new Runnable() {
                    public void run() {
                        if (apiCallback != null) {
                            apiCallback.onNetworkError(exc);
                        }
                    }
                });
            }

            public void onMatrixError(final MatrixError matrixError) {
                uploadKeysDone(matrixError.getMessage());
                MXCrypto.this.getUIHandler().post(new Runnable() {
                    public void run() {
                        if (apiCallback != null) {
                            apiCallback.onMatrixError(matrixError);
                        }
                    }
                });
            }

            public void onUnexpectedError(final Exception exc) {
                uploadKeysDone(exc.getMessage());
                MXCrypto.this.getUIHandler().post(new Runnable() {
                    public void run() {
                        if (apiCallback != null) {
                            apiCallback.onUnexpectedError(exc);
                        }
                    }
                });
            }
        });
    }

    private void uploadOneTimeKeys(final ApiCallback<KeysUploadResponse> apiCallback) {
        final Map oneTimeKeys = this.mOlmDevice.getOneTimeKeys();
        HashMap hashMap = new HashMap();
        Map map = (Map) oneTimeKeys.get("curve25519");
        if (map != null) {
            for (String str : map.keySet()) {
                HashMap hashMap2 = new HashMap();
                hashMap2.put("key", map.get(str));
                String signJSON = this.mOlmDevice.signJSON(hashMap2);
                HashMap hashMap3 = new HashMap();
                StringBuilder sb = new StringBuilder();
                sb.append("ed25519:");
                sb.append(this.mMyDevice.deviceId);
                hashMap3.put(sb.toString(), signJSON);
                HashMap hashMap4 = new HashMap();
                hashMap4.put(this.mSession.getMyUserId(), hashMap3);
                hashMap2.put("signatures", hashMap4);
                StringBuilder sb2 = new StringBuilder();
                sb2.append("signed_curve25519:");
                sb2.append(str);
                hashMap.put(sb2.toString(), hashMap2);
            }
        }
        this.mSession.getCryptoRestClient().uploadKeys(null, hashMap, this.mMyDevice.deviceId, new SimpleApiCallback<KeysUploadResponse>(apiCallback) {
            public void onSuccess(final KeysUploadResponse keysUploadResponse) {
                MXCrypto.this.getEncryptingThreadHandler().post(new Runnable() {
                    public void run() {
                        if (!MXCrypto.this.hasBeenReleased()) {
                            MXCrypto.this.mLastPublishedOneTimeKeys = oneTimeKeys;
                            MXCrypto.this.mOlmDevice.markKeysAsPublished();
                            if (apiCallback != null) {
                                MXCrypto.this.getUIHandler().post(new Runnable() {
                                    public void run() {
                                        apiCallback.onSuccess(keysUploadResponse);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public IMXDecrypting getRoomDecryptor(String str, String str2) {
        IMXDecrypting iMXDecrypting;
        if (TextUtils.isEmpty(str2)) {
            Log.m211e(LOG_TAG, "## getRoomDecryptor() : null algorithm");
            return null;
        } else if (this.mRoomDecryptors == null) {
            Log.m211e(LOG_TAG, "## getRoomDecryptor() : null mRoomDecryptors");
            return null;
        } else {
            if (!TextUtils.isEmpty(str)) {
                synchronized (this.mRoomDecryptors) {
                    if (!this.mRoomDecryptors.containsKey(str)) {
                        this.mRoomDecryptors.put(str, new HashMap());
                    }
                    iMXDecrypting = (IMXDecrypting) ((HashMap) this.mRoomDecryptors.get(str)).get(str2);
                }
                if (iMXDecrypting != null) {
                    return iMXDecrypting;
                }
            } else {
                iMXDecrypting = null;
            }
            Class decryptorClassForAlgorithm = MXCryptoAlgorithms.sharedAlgorithms().decryptorClassForAlgorithm(str2);
            if (decryptorClassForAlgorithm != null) {
                try {
                    iMXDecrypting = (IMXDecrypting) decryptorClassForAlgorithm.getConstructors()[0].newInstance(new Object[0]);
                    if (iMXDecrypting != null) {
                        iMXDecrypting.initWithMatrixSession(this.mSession);
                        if (!TextUtils.isEmpty(str)) {
                            synchronized (this.mRoomDecryptors) {
                                ((HashMap) this.mRoomDecryptors.get(str)).put(str2, iMXDecrypting);
                            }
                        }
                    }
                } catch (Exception unused) {
                    Log.m211e(LOG_TAG, "## getRoomDecryptor() : fail to load the class");
                    return null;
                }
            }
            return iMXDecrypting;
        }
    }

    public void exportRoomKeys(String str, ApiCallback<byte[]> apiCallback) {
        exportRoomKeys(str, MXMegolmExportEncryption.DEFAULT_ITERATION_COUNT, apiCallback);
    }

    public void exportRoomKeys(final String str, int i, final ApiCallback<byte[]> apiCallback) {
        final int max = Math.max(0, i);
        getDecryptingThreadHandler().post(new Runnable() {
            public void run() {
                if (MXCrypto.this.mCryptoStore == null) {
                    MXCrypto.this.getUIHandler().post(new Runnable() {
                        public void run() {
                            apiCallback.onSuccess(new byte[0]);
                        }
                    });
                    return;
                }
                ArrayList arrayList = new ArrayList();
                for (MXOlmInboundGroupSession2 exportKeys : MXCrypto.this.mCryptoStore.getInboundGroupSessions()) {
                    Map exportKeys2 = exportKeys.exportKeys();
                    if (exportKeys2 != null) {
                        arrayList.add(exportKeys2);
                    }
                }
                try {
                    final byte[] encryptMegolmKeyFile = MXMegolmExportEncryption.encryptMegolmKeyFile(JsonUtils.getGson(false).toJsonTree(arrayList).toString(), str, max);
                    MXCrypto.this.getUIHandler().post(new Runnable() {
                        public void run() {
                            apiCallback.onSuccess(encryptMegolmKeyFile);
                        }
                    });
                } catch (Exception e) {
                    apiCallback.onUnexpectedError(e);
                }
            }
        });
    }

    public void importRoomKeys(final byte[] bArr, final String str, final ApiCallback<Void> apiCallback) {
        getDecryptingThreadHandler().post(new Runnable() {
            public void run() {
                long j;
                C247035 r1 = this;
                long currentTimeMillis = System.currentTimeMillis();
                try {
                    String decryptMegolmKeyFile = MXMegolmExportEncryption.decryptMegolmKeyFile(bArr, str);
                    long currentTimeMillis2 = System.currentTimeMillis();
                    Log.m209d(MXCrypto.LOG_TAG, "## importRoomKeys starts");
                    int i = 0;
                    try {
                        List list = (List) JsonUtils.getGson(false).fromJson(decryptMegolmKeyFile, new TypeToken<List<Map<String, Object>>>() {
                        }.getType());
                        long currentTimeMillis3 = System.currentTimeMillis();
                        String access$000 = MXCrypto.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## importRoomKeys retrieve ");
                        sb.append(list.size());
                        sb.append("sessions in ");
                        long j2 = currentTimeMillis2 - currentTimeMillis;
                        sb.append(j2);
                        sb.append(" ms");
                        Log.m209d(access$000, sb.toString());
                        while (i < list.size()) {
                            Map map = (Map) list.get(i);
                            MXOlmInboundGroupSession2 importInboundGroupSession = MXCrypto.this.mOlmDevice.importInboundGroupSession(map);
                            if (importInboundGroupSession != null && MXCrypto.this.mRoomDecryptors.containsKey(importInboundGroupSession.mRoomId)) {
                                IMXDecrypting iMXDecrypting = (IMXDecrypting) ((HashMap) MXCrypto.this.mRoomDecryptors.get(importInboundGroupSession.mRoomId)).get(map.get("algorithm"));
                                if (iMXDecrypting != null) {
                                    try {
                                        String sessionIdentifier = importInboundGroupSession.mSession.sessionIdentifier();
                                        String access$0002 = MXCrypto.LOG_TAG;
                                        StringBuilder sb2 = new StringBuilder();
                                        j = currentTimeMillis2;
                                        try {
                                            sb2.append("## importRoomKeys retrieve mSenderKey ");
                                            sb2.append(importInboundGroupSession.mSenderKey);
                                            sb2.append(" sessionId ");
                                            sb2.append(sessionIdentifier);
                                            Log.m209d(access$0002, sb2.toString());
                                            iMXDecrypting.onNewSession(importInboundGroupSession.mSenderKey, sessionIdentifier);
                                        } catch (Exception e) {
                                            e = e;
                                        }
                                    } catch (Exception e2) {
                                        e = e2;
                                        j = currentTimeMillis2;
                                        Exception exc = e;
                                        String access$0003 = MXCrypto.LOG_TAG;
                                        StringBuilder sb3 = new StringBuilder();
                                        sb3.append("## importRoomKeys() : onNewSession failed ");
                                        sb3.append(exc.getMessage());
                                        Log.m211e(access$0003, sb3.toString());
                                        i++;
                                        currentTimeMillis2 = j;
                                        r1 = this;
                                    }
                                    i++;
                                    currentTimeMillis2 = j;
                                    r1 = this;
                                }
                            }
                            j = currentTimeMillis2;
                            i++;
                            currentTimeMillis2 = j;
                            r1 = this;
                        }
                        long j3 = currentTimeMillis2;
                        long currentTimeMillis4 = System.currentTimeMillis();
                        String access$0004 = MXCrypto.LOG_TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("## importRoomKeys : done in ");
                        sb4.append(currentTimeMillis4 - currentTimeMillis);
                        sb4.append(" ms (");
                        sb4.append(list.size());
                        sb4.append(" sessions)");
                        Log.m209d(access$0004, sb4.toString());
                        String access$0005 = MXCrypto.LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("## importRoomKeys : decryptMegolmKeyFile done in ");
                        sb5.append(j2);
                        sb5.append(" ms");
                        Log.m209d(access$0005, sb5.toString());
                        String access$0006 = MXCrypto.LOG_TAG;
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("## importRoomKeys : JSON parsing ");
                        sb6.append(currentTimeMillis3 - j3);
                        sb6.append(" ms");
                        Log.m209d(access$0006, sb6.toString());
                        String access$0007 = MXCrypto.LOG_TAG;
                        StringBuilder sb7 = new StringBuilder();
                        sb7.append("## importRoomKeys : sessions import ");
                        sb7.append(currentTimeMillis4 - currentTimeMillis3);
                        sb7.append(" ms");
                        Log.m209d(access$0007, sb7.toString());
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                apiCallback.onSuccess(null);
                            }
                        });
                    } catch (Exception e3) {
                        final Exception exc2 = e3;
                        String access$0008 = MXCrypto.LOG_TAG;
                        StringBuilder sb8 = new StringBuilder();
                        sb8.append("## importRoomKeys failed ");
                        sb8.append(exc2.getMessage());
                        Log.m211e(access$0008, sb8.toString());
                        MXCrypto.this.getUIHandler().post(new Runnable() {
                            public void run() {
                                apiCallback.onUnexpectedError(exc2);
                            }
                        });
                    }
                } catch (Exception e4) {
                    final Exception exc3 = e4;
                    MXCrypto.this.getUIHandler().post(new Runnable() {
                        public void run() {
                            apiCallback.onUnexpectedError(exc3);
                        }
                    });
                }
            }
        });
    }

    public boolean warnOnUnknownDevices() {
        return this.mWarnOnUnknownDevices;
    }

    public void setWarnOnUnknownDevices(boolean z) {
        this.mWarnOnUnknownDevices = z;
    }

    public static MXUsersDevicesMap<MXDeviceInfo> getUnknownDevices(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
        MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap2 = new MXUsersDevicesMap<>();
        for (String str : mXUsersDevicesMap.getUserIds()) {
            for (String str2 : mXUsersDevicesMap.getUserDeviceIds(str)) {
                MXDeviceInfo mXDeviceInfo = (MXDeviceInfo) mXUsersDevicesMap.getObject(str2, str);
                if (mXDeviceInfo.isUnknown()) {
                    mXUsersDevicesMap2.setObject(mXDeviceInfo, str, str2);
                }
            }
        }
        return mXUsersDevicesMap2;
    }

    public void checkUnknownDevices(List<String> list, final ApiCallback<Void> apiCallback) {
        this.mDevicesList.downloadKeys(list, true, new SimpleApiCallback<MXUsersDevicesMap<MXDeviceInfo>>(apiCallback) {
            public void onSuccess(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                MXUsersDevicesMap unknownDevices = MXCrypto.getUnknownDevices(mXUsersDevicesMap);
                if (unknownDevices.getMap().size() == 0) {
                    apiCallback.onSuccess(null);
                } else {
                    apiCallback.onMatrixError(new MXCryptoError(MXCryptoError.UNKNOWN_DEVICES_CODE, MXCryptoError.UNABLE_TO_ENCRYPT, MXCryptoError.UNKNOWN_DEVICES_REASON, unknownDevices));
                }
            }
        });
    }

    public void setGlobalBlacklistUnverifiedDevices(final boolean z, final ApiCallback<Void> apiCallback) {
        getEncryptingThreadHandler().post(new Runnable() {
            public void run() {
                MXCrypto.this.mCryptoStore.setGlobalBlacklistUnverifiedDevices(z);
                MXCrypto.this.getUIHandler().post(new Runnable() {
                    public void run() {
                        if (apiCallback != null) {
                            apiCallback.onSuccess(null);
                        }
                    }
                });
            }
        });
    }

    public boolean getGlobalBlacklistUnverifiedDevices() {
        return this.mCryptoStore.getGlobalBlacklistUnverifiedDevices();
    }

    public void getGlobalBlacklistUnverifiedDevices(final ApiCallback<Boolean> apiCallback) {
        getEncryptingThreadHandler().post(new Runnable() {
            public void run() {
                if (apiCallback != null) {
                    final boolean globalBlacklistUnverifiedDevices = MXCrypto.this.getGlobalBlacklistUnverifiedDevices();
                    MXCrypto.this.getUIHandler().post(new Runnable() {
                        public void run() {
                            apiCallback.onSuccess(Boolean.valueOf(globalBlacklistUnverifiedDevices));
                        }
                    });
                }
            }
        });
    }

    public boolean isRoomBlacklistUnverifiedDevices(String str) {
        if (str != null) {
            return this.mCryptoStore.getRoomsListBlacklistUnverifiedDevices().contains(str);
        }
        return false;
    }

    public void isRoomBlacklistUnverifiedDevices(final String str, final ApiCallback<Boolean> apiCallback) {
        getEncryptingThreadHandler().post(new Runnable() {
            public void run() {
                final boolean isRoomBlacklistUnverifiedDevices = MXCrypto.this.isRoomBlacklistUnverifiedDevices(str);
                MXCrypto.this.getUIHandler().post(new Runnable() {
                    public void run() {
                        if (apiCallback != null) {
                            apiCallback.onSuccess(Boolean.valueOf(isRoomBlacklistUnverifiedDevices));
                        }
                    }
                });
            }
        });
    }

    private void setRoomBlacklistUnverifiedDevices(final String str, final boolean z, final ApiCallback<Void> apiCallback) {
        if (this.mSession.getDataHandler().getRoom(str) == null) {
            getUIHandler().post(new Runnable() {
                public void run() {
                    apiCallback.onSuccess(null);
                }
            });
        } else {
            getEncryptingThreadHandler().post(new Runnable() {
                public void run() {
                    List roomsListBlacklistUnverifiedDevices = MXCrypto.this.mCryptoStore.getRoomsListBlacklistUnverifiedDevices();
                    if (!z) {
                        roomsListBlacklistUnverifiedDevices.remove(str);
                    } else if (!roomsListBlacklistUnverifiedDevices.contains(str)) {
                        roomsListBlacklistUnverifiedDevices.add(str);
                    }
                    MXCrypto.this.mCryptoStore.setRoomsListBlacklistUnverifiedDevices(roomsListBlacklistUnverifiedDevices);
                    MXCrypto.this.getUIHandler().post(new Runnable() {
                        public void run() {
                            if (apiCallback != null) {
                                apiCallback.onSuccess(null);
                            }
                        }
                    });
                }
            });
        }
    }

    public void setRoomBlacklistUnverifiedDevices(String str, ApiCallback<Void> apiCallback) {
        setRoomBlacklistUnverifiedDevices(str, true, apiCallback);
    }

    public void setRoomUnblacklistUnverifiedDevices(String str, ApiCallback<Void> apiCallback) {
        setRoomBlacklistUnverifiedDevices(str, false, apiCallback);
    }

    public void requestRoomKey(final Map<String, String> map, final List<Map<String, String>> list) {
        getEncryptingThreadHandler().post(new Runnable() {
            public void run() {
                MXCrypto.this.mOutgoingRoomKeyRequestManager.sendRoomKeyRequest(map, list);
            }
        });
    }

    public void cancelRoomKeyRequest(final Map<String, String> map) {
        getEncryptingThreadHandler().post(new Runnable() {
            public void run() {
                MXCrypto.this.mOutgoingRoomKeyRequestManager.cancelRoomKeyRequest(map);
            }
        });
    }

    public void reRequestRoomKeyForEvent(@NonNull Event event) {
        if (event.getWireContent().isJsonObject()) {
            JsonObject asJsonObject = event.getWireContent().getAsJsonObject();
            final String asString = asJsonObject.get("algorithm").getAsString();
            final String asString2 = asJsonObject.get("sender_key").getAsString();
            final String asString3 = asJsonObject.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID).getAsString();
            Handler encryptingThreadHandler = getEncryptingThreadHandler();
            final Event event2 = event;
            C249244 r2 = new Runnable() {
                public void run() {
                    HashMap hashMap = new HashMap();
                    hashMap.put("room_id", event2.roomId);
                    hashMap.put("algorithm", asString);
                    hashMap.put("sender_key", asString2);
                    hashMap.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID, asString3);
                    MXCrypto.this.mOutgoingRoomKeyRequestManager.resendRoomKeyRequest(hashMap);
                }
            };
            encryptingThreadHandler.post(r2);
        }
    }

    public void addRoomKeysRequestListener(IRoomKeysRequestListener iRoomKeysRequestListener) {
        synchronized (this.mRoomKeysRequestListeners) {
            this.mRoomKeysRequestListeners.add(iRoomKeysRequestListener);
        }
    }

    public void removeRoomKeysRequestListener(IRoomKeysRequestListener iRoomKeysRequestListener) {
        synchronized (this.mRoomKeysRequestListeners) {
            this.mRoomKeysRequestListeners.remove(iRoomKeysRequestListener);
        }
    }

    private void onRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest) {
        synchronized (this.mRoomKeysRequestListeners) {
            for (IRoomKeysRequestListener onRoomKeyRequest : this.mRoomKeysRequestListeners) {
                try {
                    onRoomKeyRequest.onRoomKeyRequest(incomingRoomKeyRequest);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onRoomKeyRequest() failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }

    private void onRoomKeyRequestCancellation(IncomingRoomKeyRequestCancellation incomingRoomKeyRequestCancellation) {
        synchronized (this.mRoomKeysRequestListeners) {
            for (IRoomKeysRequestListener onRoomKeyRequestCancellation : this.mRoomKeysRequestListeners) {
                try {
                    onRoomKeyRequestCancellation.onRoomKeyRequestCancellation(incomingRoomKeyRequestCancellation);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onRoomKeyRequestCancellation() failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }
}
