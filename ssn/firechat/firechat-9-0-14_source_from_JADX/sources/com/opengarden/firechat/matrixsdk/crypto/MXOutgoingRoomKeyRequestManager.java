package com.opengarden.firechat.matrixsdk.crypto;

import android.os.Handler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.OutgoingRoomKeyRequest.RequestState;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.cryptostore.IMXCryptoStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyRequest;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MXOutgoingRoomKeyRequestManager {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXOutgoingRoomKeyRequestManager";
    private static final int SEND_KEY_REQUESTS_DELAY_MS = 500;
    public boolean mClientRunning;
    /* access modifiers changed from: private */
    public IMXCryptoStore mCryptoStore;
    /* access modifiers changed from: private */
    public boolean mSendOutgoingRoomKeyRequestsRunning;
    private MXSession mSession;
    private int mTxnCtr;
    /* access modifiers changed from: private */
    public Handler mWorkingHandler;

    public MXOutgoingRoomKeyRequestManager(MXSession mXSession, MXCrypto mXCrypto) {
        this.mSession = mXSession;
        this.mWorkingHandler = mXCrypto.getEncryptingThreadHandler();
        this.mCryptoStore = mXCrypto.getCryptoStore();
    }

    public void start() {
        this.mClientRunning = true;
        startTimer();
    }

    public void stop() {
        this.mClientRunning = false;
    }

    /* access modifiers changed from: private */
    public String makeTxnId() {
        StringBuilder sb = new StringBuilder();
        sb.append("m");
        sb.append(System.currentTimeMillis());
        sb.append(".");
        int i = this.mTxnCtr;
        this.mTxnCtr = i + 1;
        sb.append(i);
        return sb.toString();
    }

    public void sendRoomKeyRequest(final Map<String, String> map, final List<Map<String, String>> list) {
        this.mWorkingHandler.post(new Runnable() {
            public void run() {
                if (MXOutgoingRoomKeyRequestManager.this.mCryptoStore.getOrAddOutgoingRoomKeyRequest(new OutgoingRoomKeyRequest(map, list, MXOutgoingRoomKeyRequestManager.this.makeTxnId(), RequestState.UNSENT)).mState == RequestState.UNSENT) {
                    MXOutgoingRoomKeyRequestManager.this.startTimer();
                }
            }
        });
    }

    public void cancelRoomKeyRequest(Map<String, String> map) {
        cancelRoomKeyRequest(map, false);
    }

    public void resendRoomKeyRequest(Map<String, String> map) {
        cancelRoomKeyRequest(map, true);
    }

    private void cancelRoomKeyRequest(Map<String, String> map, boolean z) {
        OutgoingRoomKeyRequest outgoingRoomKeyRequest = this.mCryptoStore.getOutgoingRoomKeyRequest(map);
        if (outgoingRoomKeyRequest != null && outgoingRoomKeyRequest.mState != RequestState.CANCELLATION_PENDING && outgoingRoomKeyRequest.mState != RequestState.CANCELLATION_PENDING_AND_WILL_RESEND) {
            if (outgoingRoomKeyRequest.mState == RequestState.UNSENT || outgoingRoomKeyRequest.mState == RequestState.FAILED) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## cancelRoomKeyRequest() : deleting unnecessary room key request for ");
                sb.append(map);
                Log.m209d(str, sb.toString());
                this.mCryptoStore.deleteOutgoingRoomKeyRequest(outgoingRoomKeyRequest.mRequestId);
            } else if (outgoingRoomKeyRequest.mState == RequestState.SENT) {
                if (z) {
                    outgoingRoomKeyRequest.mState = RequestState.CANCELLATION_PENDING_AND_WILL_RESEND;
                } else {
                    outgoingRoomKeyRequest.mState = RequestState.CANCELLATION_PENDING;
                }
                outgoingRoomKeyRequest.mCancellationTxnId = makeTxnId();
                this.mCryptoStore.updateOutgoingRoomKeyRequest(outgoingRoomKeyRequest);
                sendOutgoingRoomKeyRequestCancellation(outgoingRoomKeyRequest);
            }
        }
    }

    /* access modifiers changed from: private */
    public void startTimer() {
        this.mWorkingHandler.post(new Runnable() {
            public void run() {
                if (!MXOutgoingRoomKeyRequestManager.this.mSendOutgoingRoomKeyRequestsRunning) {
                    MXOutgoingRoomKeyRequestManager.this.mWorkingHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (MXOutgoingRoomKeyRequestManager.this.mSendOutgoingRoomKeyRequestsRunning) {
                                Log.m209d(MXOutgoingRoomKeyRequestManager.LOG_TAG, "## startTimer() : RoomKeyRequestSend already in progress!");
                                return;
                            }
                            MXOutgoingRoomKeyRequestManager.this.mSendOutgoingRoomKeyRequestsRunning = true;
                            MXOutgoingRoomKeyRequestManager.this.sendOutgoingRoomKeyRequests();
                        }
                    }, 500);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void sendOutgoingRoomKeyRequests() {
        if (!this.mClientRunning) {
            this.mSendOutgoingRoomKeyRequestsRunning = false;
            return;
        }
        Log.m209d(LOG_TAG, "## sendOutgoingRoomKeyRequests() :  Looking for queued outgoing room key requests");
        OutgoingRoomKeyRequest outgoingRoomKeyRequestByState = this.mCryptoStore.getOutgoingRoomKeyRequestByState(new HashSet(Arrays.asList(new RequestState[]{RequestState.UNSENT, RequestState.CANCELLATION_PENDING, RequestState.CANCELLATION_PENDING_AND_WILL_RESEND})));
        if (outgoingRoomKeyRequestByState == null) {
            Log.m211e(LOG_TAG, "## sendOutgoingRoomKeyRequests() : No more outgoing room key requests");
            this.mSendOutgoingRoomKeyRequestsRunning = false;
            return;
        }
        if (RequestState.UNSENT == outgoingRoomKeyRequestByState.mState) {
            sendOutgoingRoomKeyRequest(outgoingRoomKeyRequestByState);
        } else {
            sendOutgoingRoomKeyRequestCancellation(outgoingRoomKeyRequestByState);
        }
    }

    private void sendOutgoingRoomKeyRequest(final OutgoingRoomKeyRequest outgoingRoomKeyRequest) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## sendOutgoingRoomKeyRequest() : Requesting keys ");
        sb.append(outgoingRoomKeyRequest.mRequestBody);
        sb.append(" from ");
        sb.append(outgoingRoomKeyRequest.mRecipients);
        sb.append(" id ");
        sb.append(outgoingRoomKeyRequest.mRequestId);
        Log.m209d(str, sb.toString());
        HashMap hashMap = new HashMap();
        hashMap.put("action", "request");
        hashMap.put("requesting_device_id", this.mCryptoStore.getDeviceId());
        hashMap.put("request_id", outgoingRoomKeyRequest.mRequestId);
        hashMap.put("body", outgoingRoomKeyRequest.mRequestBody);
        sendMessageToDevices(hashMap, outgoingRoomKeyRequest.mRecipients, outgoingRoomKeyRequest.mRequestId, new ApiCallback<Void>() {
            private void onDone(final RequestState requestState) {
                MXOutgoingRoomKeyRequestManager.this.mWorkingHandler.post(new Runnable() {
                    public void run() {
                        if (outgoingRoomKeyRequest.mState != RequestState.UNSENT) {
                            String access$400 = MXOutgoingRoomKeyRequestManager.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## sendOutgoingRoomKeyRequest() : Cannot update room key request from UNSENT as it was already updated to ");
                            sb.append(outgoingRoomKeyRequest.mState);
                            Log.m209d(access$400, sb.toString());
                        } else {
                            outgoingRoomKeyRequest.mState = requestState;
                            MXOutgoingRoomKeyRequestManager.this.mCryptoStore.updateOutgoingRoomKeyRequest(outgoingRoomKeyRequest);
                        }
                        MXOutgoingRoomKeyRequestManager.this.mSendOutgoingRoomKeyRequestsRunning = false;
                        MXOutgoingRoomKeyRequestManager.this.startTimer();
                    }
                });
            }

            public void onSuccess(Void voidR) {
                Log.m209d(MXOutgoingRoomKeyRequestManager.LOG_TAG, "## sendOutgoingRoomKeyRequest succeed");
                onDone(RequestState.SENT);
            }

            public void onNetworkError(Exception exc) {
                String access$400 = MXOutgoingRoomKeyRequestManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendOutgoingRoomKeyRequest failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$400, sb.toString());
                onDone(RequestState.FAILED);
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$400 = MXOutgoingRoomKeyRequestManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendOutgoingRoomKeyRequest failed ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$400, sb.toString());
                onDone(RequestState.FAILED);
            }

            public void onUnexpectedError(Exception exc) {
                String access$400 = MXOutgoingRoomKeyRequestManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendOutgoingRoomKeyRequest failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$400, sb.toString());
                onDone(RequestState.FAILED);
            }
        });
    }

    private void sendOutgoingRoomKeyRequestCancellation(final OutgoingRoomKeyRequest outgoingRoomKeyRequest) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## sendOutgoingRoomKeyRequestCancellation() : Sending cancellation for key request for ");
        sb.append(outgoingRoomKeyRequest.mRequestBody);
        sb.append(" to ");
        sb.append(outgoingRoomKeyRequest.mRecipients);
        sb.append(" cancellation id  ");
        sb.append(outgoingRoomKeyRequest.mCancellationTxnId);
        Log.m209d(str, sb.toString());
        HashMap hashMap = new HashMap();
        hashMap.put("action", RoomKeyRequest.ACTION_REQUEST_CANCELLATION);
        hashMap.put("requesting_device_id", this.mCryptoStore.getDeviceId());
        hashMap.put("request_id", outgoingRoomKeyRequest.mCancellationTxnId);
        sendMessageToDevices(hashMap, outgoingRoomKeyRequest.mRecipients, outgoingRoomKeyRequest.mCancellationTxnId, new ApiCallback<Void>() {
            private void onDone() {
                MXOutgoingRoomKeyRequestManager.this.mWorkingHandler.post(new Runnable() {
                    public void run() {
                        MXOutgoingRoomKeyRequestManager.this.mCryptoStore.deleteOutgoingRoomKeyRequest(outgoingRoomKeyRequest.mRequestId);
                        MXOutgoingRoomKeyRequestManager.this.mSendOutgoingRoomKeyRequestsRunning = false;
                        MXOutgoingRoomKeyRequestManager.this.startTimer();
                    }
                });
            }

            public void onSuccess(Void voidR) {
                Log.m209d(MXOutgoingRoomKeyRequestManager.LOG_TAG, "## sendOutgoingRoomKeyRequestCancellation() : done");
                boolean z = outgoingRoomKeyRequest.mState == RequestState.CANCELLATION_PENDING_AND_WILL_RESEND;
                onDone();
                if (z) {
                    MXOutgoingRoomKeyRequestManager.this.sendRoomKeyRequest(outgoingRoomKeyRequest.mRequestBody, outgoingRoomKeyRequest.mRecipients);
                }
            }

            public void onNetworkError(Exception exc) {
                String access$400 = MXOutgoingRoomKeyRequestManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendOutgoingRoomKeyRequestCancellation failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$400, sb.toString());
                onDone();
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$400 = MXOutgoingRoomKeyRequestManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendOutgoingRoomKeyRequestCancellation failed ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$400, sb.toString());
                onDone();
            }

            public void onUnexpectedError(Exception exc) {
                String access$400 = MXOutgoingRoomKeyRequestManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sendOutgoingRoomKeyRequestCancellation failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$400, sb.toString());
                onDone();
            }
        });
    }

    private void sendMessageToDevices(Map<String, Object> map, List<Map<String, String>> list, String str, ApiCallback<Void> apiCallback) {
        MXUsersDevicesMap mXUsersDevicesMap = new MXUsersDevicesMap();
        for (Map map2 : list) {
            mXUsersDevicesMap.setObject(map, (String) map2.get("userId"), (String) map2.get("deviceId"));
        }
        this.mSession.getCryptoRestClient().sendToDevice(Event.EVENT_TYPE_ROOM_KEY_REQUEST, mXUsersDevicesMap, str, apiCallback);
    }
}
